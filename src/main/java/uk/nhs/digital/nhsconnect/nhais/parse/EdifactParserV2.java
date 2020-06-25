package uk.nhs.digital.nhsconnect.nhais.parse;

import com.google.common.collect.Comparators;
import com.google.common.collect.Streams;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageHeader;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.MessageTrailer;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.SegmentGroup;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.Split;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.message.ToEdifactParsingException;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.InterchangeV2;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.MessageV2;
import uk.nhs.digital.nhsconnect.nhais.model.edifact.v2.TransactionV2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EdifactParserV2 {
    public InterchangeV2 parse(String edifact) {
        var allEdifactSegments = Arrays.asList(Split.bySegmentTerminator(edifact.replaceAll("\\n", "").strip()));

        var interchange = parseInterchange(allEdifactSegments);

        validateInterchange(interchange);

        return interchange;
    }

    private InterchangeV2 parseInterchange(List<String> allEdifactSegments) {
        InterchangeV2 interchange = new InterchangeV2(extractInterchangeLines(allEdifactSegments));

        var messages = parseAllMessages(allEdifactSegments);
        messages.forEach(message -> message.setInterchange(interchange));
        interchange.setMessages(messages);

        return interchange;
    }

    private List<MessageV2> parseAllMessages(List<String> allEdifactSegments) {
        var allMessageHeaderSegmentIndexes = findAllIndexesOfSegment(allEdifactSegments, MessageHeader.KEY);
        var allMessageTrailerSegmentIndexes = findAllIndexesOfSegment(allEdifactSegments, MessageTrailer.KEY);

        var messageHeaderTrailerIndexPairs = zipIndexes(allMessageHeaderSegmentIndexes, allMessageTrailerSegmentIndexes);

        return messageHeaderTrailerIndexPairs.stream()
            .map(messageStartEndIndexPair ->
                allEdifactSegments.subList(messageStartEndIndexPair.getLeft(), messageStartEndIndexPair.getRight() + 1))
            .map(this::parseMessage)
            .collect(Collectors.toList());
    }

    private MessageV2 parseMessage(List<String> singleMessageEdifactSegments) {
        var transactionStartIndexes = findAllIndexesOfSegment(singleMessageEdifactSegments, SegmentGroup.KEY_01);
        var transactionEndIndexes = new ArrayList<>(transactionStartIndexes);
        // there is no transaction end indicator, so ending segment is the one before the beginning of the next transaction
        // so end indexes are beginning without first S01
        transactionEndIndexes.remove(0);
        // and last transaction end indicator is the segment before message trailer
        transactionEndIndexes.add(singleMessageEdifactSegments.size() - 1);

        var transactionStartEndIndexPairs = zipIndexes(transactionStartIndexes, transactionEndIndexes);

        var transactions = transactionStartEndIndexPairs.stream()
            .map(transactionStartEndIndexPair ->
                singleMessageEdifactSegments.subList(transactionStartEndIndexPair.getLeft(), transactionStartEndIndexPair.getRight()))
            .map(TransactionV2::new)
            .collect(Collectors.toList());

        var onlyMessageLines = new ArrayList<>(singleMessageEdifactSegments.subList(0, transactionStartIndexes.get(0))); // first lines until transaction
        onlyMessageLines.add(singleMessageEdifactSegments.get(singleMessageEdifactSegments.size() - 1)); // message trailer

        var message = new MessageV2(onlyMessageLines);
        message.setTransactions(transactions);
        transactions.forEach(transaction -> transaction.setMessage(message));

        return message;
    }

    private List<Pair<Integer, Integer>> zipIndexes(List<Integer> startIndexes, List<Integer> endIndexes) {
        if (startIndexes.size() != endIndexes.size()) {
            throw new ToEdifactParsingException(
                "Message header-trailer count mismatch: " + startIndexes.size() + "-" + endIndexes.size());
        }

        var indexPairs = Streams.zip(startIndexes.stream(), endIndexes.stream(), Pair::of)
            .collect(Collectors.toList());

        if (!areIndexesInOrder(indexPairs)) {
            throw new ToEdifactParsingException("Message trailer before message header");
        }

        return indexPairs;
    }

    private boolean areIndexesInOrder(List<Pair<Integer, Integer>> messageIndexPairs) {
        return Comparators.isInOrder(
            messageIndexPairs.stream()
                .map(pair -> List.of(pair.getLeft(), pair.getRight()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList()),
            Comparator.naturalOrder());
    }

    private List<String> extractInterchangeLines(List<String> allEdifactSegments) {
        var firstMessageHeaderIndex = findAllIndexesOfSegment(allEdifactSegments, MessageHeader.KEY).get(0);
        var allMessageTrailerIndexes = findAllIndexesOfSegment(allEdifactSegments, MessageTrailer.KEY);
        var lastMessageTrailerIndex = allMessageTrailerIndexes.get(allMessageTrailerIndexes.size() - 1);

        var segmentsBeforeFirstMessageHeader = allEdifactSegments.subList(0, firstMessageHeaderIndex);
        var segmentsAfterLastMessageTrailer = allEdifactSegments.subList(lastMessageTrailerIndex + 1, allEdifactSegments.size());

        return Stream.of(segmentsBeforeFirstMessageHeader, segmentsAfterLastMessageTrailer)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private List<Integer> findAllIndexesOfSegment(List<String> list, String key) {
        var indexes = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).startsWith(key)) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private void validateInterchange(InterchangeV2 interchange) {
        List<ToEdifactParsingException> exceptions = interchange.validate();
        String errorList = exceptions.stream()
            .map(Exception::getMessage)
            .collect(Collectors.joining(", "));

        if (StringUtils.isNotEmpty(errorList)) {
            throw new ToEdifactParsingException(errorList);
        }
    }
}
