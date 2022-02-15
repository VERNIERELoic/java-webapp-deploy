#Déclaration de l'instance à créer
resource "openstack_compute_instance_v2" "infra_front" {
      name            = "front"
      image_name      = "Ubuntu 21.04"
      flavor_name     = "d2-2"
      key_pair        = openstack_compute_keypair_v2.My-Key.name
      security_groups = [openstack_networking_secgroup_v2.allow_ssh_icmp.name, openstack_networking_secgroup_v2.allow_infra.name]
      network  {
          name = "Ext-Net"
      }
      count = 2
}
