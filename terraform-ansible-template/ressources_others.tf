resource "openstack_compute_instance_v2" "infra_bdd" {
      name            = "bdd"
      image_name      = "Ubuntu 21.04"
      flavor_name     = "d2-2"
      security_groups = [openstack_networking_secgroup_v2.allow_ssh_icmp.name, openstack_networking_secgroup_v2.allow_postgres.name]
      key_pair        = openstack_compute_keypair_v2.My-Key.name
      power_state     = var.powerstate
      network  {
        name = "Ext-Net"
      }
      count = 1
}


resource "openstack_compute_instance_v2" "infra_reverse_proxy" {
      name            = "reverse_proxy"
      image_name      = "Ubuntu 21.04"
      flavor_name     = "d2-2"
      key_pair    = openstack_compute_keypair_v2.My-Key.name
      security_groups = [openstack_networking_secgroup_v2.allow_ssh_icmp.name, openstack_networking_secgroup_v2.allow_infra.name]
      power_state     = var.powerstate
      network  {
        name = "Ext-Net"
      }
      count = 1 
}