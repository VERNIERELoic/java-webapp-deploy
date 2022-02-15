resource "openstack_compute_keypair_v2" "My-Key" {
  name       = "Groupe-Cle" # Attention, est unique à travers le tenant !
  public_key = file("~/.ssh/id_openstack.pub") # Valeur par défaut
}

resource "openstack_networking_secgroup_v2" "allow_ssh_icmp" {
  name        = "allow_ssh_icmp" # Attention, est unique à travers le tenant !
}

resource "openstack_networking_secgroup_v2" "allow_infra" {
  name        = "allow_infra" # Attention, est unique à travers le tenant !
}

resource "openstack_networking_secgroup_v2" "allow_postgres" {
  name        = "allow_postgres"
}

#Déclaration d'une ressouce pour ajouter ma clé SSH sur OpenStack
resource "openstack_networking_secgroup_rule_v2" "allow_ssh" {
  direction         = "ingress"
  ethertype         = "IPv4"
  protocol          = "tcp"
  port_range_min    = 22
  port_range_max    = 22
  remote_ip_prefix  = "0.0.0.0/0"
  security_group_id = openstack_networking_secgroup_v2.allow_ssh_icmp.id
}

resource "openstack_networking_secgroup_rule_v2" "allow_web" {
  direction         = "ingress"
  ethertype         = "IPv4"
  protocol          = "tcp"
  port_range_min    = 80
  port_range_max    = 80
  remote_ip_prefix  = "0.0.0.0/0"
  security_group_id = openstack_networking_secgroup_v2.allow_infra.id
}

resource "openstack_networking_secgroup_rule_v2" "allow_back" {
  direction         = "ingress"
  ethertype         = "IPv4"
  protocol          = "tcp"
  port_range_min    = 8090
  port_range_max    = 8090
  remote_ip_prefix  = "0.0.0.0/0"
  security_group_id = openstack_networking_secgroup_v2.allow_infra.id
}

resource "openstack_networking_secgroup_rule_v2" "allow_postgres" {
  direction         = "ingress"
  ethertype         = "IPv4"
  protocol          = "tcp"
  port_range_min    = 5432
  port_range_max    = 5432
  #remote_ip_prefix  = "0.0.0.0/0"
  remote_group_id = openstack_networking_secgroup_v2.allow_infra.id
  security_group_id = openstack_networking_secgroup_v2.allow_postgres.id
}

resource "openstack_networking_secgroup_rule_v2" "allow_icmp" {
  direction         = "ingress"
  ethertype         = "IPv4"
  protocol          = "icmp"
  remote_ip_prefix  = "0.0.0.0/0"
  security_group_id = openstack_networking_secgroup_v2.allow_ssh_icmp.id
}