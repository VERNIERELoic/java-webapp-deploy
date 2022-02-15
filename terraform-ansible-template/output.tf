resource "local_file" "ansible_inventory" {
  
  content = templatefile("ansible-template.tpl",
    {
     front =  openstack_compute_instance_v2.infra_front
     back =  openstack_compute_instance_v2.infra_back
     bdd =  openstack_compute_instance_v2.infra_bdd
     traefik = openstack_compute_instance_v2.infra_reverse_proxy
    }
  )
  filename = "ansible/inventory/hosts"
}
