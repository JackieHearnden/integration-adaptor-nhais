# resource "aws_route53_zone" "mhs_hosted_zone" {
#   name = "${var.environment_id}.${var.internal_root_domain}"
#   vpc {
#     # Note that if a change is made to vpc_id here, then the lifecycle block below
#     # may need to be deleted in order for this to be picked up. But care must be
#     # taken when doing this, as this can conflict with other
#     # aws_route53_zone_association blocks
#     vpc_id = aws_vpc.mhs_vpc.id
#   }

#   tags = {
#     Name = "${var.environment_id}-mhs-hosted-zone"
#     EnvironmentId = var.environment_id
#   }

#   lifecycle {
#     # Ignore changes to vpc_id as this is managed by aws_route53_zone_association resources.
#     ignore_changes = [
#       vpc
#     ]
#   }
# }