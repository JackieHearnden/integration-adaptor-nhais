output "vpc_id" {
  value = aws_vpc.base_vpc.id
}

output "base_cluster_id" {
  value = module.base_ecs_cluster.cluster_id
}