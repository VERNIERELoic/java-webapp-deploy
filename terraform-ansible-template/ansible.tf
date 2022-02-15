# resource "null_resource" "setup_vm"{
#     depends_on     = [openstack_compute_instance_v2.infra_instances
#     ]

#     provisioner "local-exec" {
#       command = <<EOF
#          cd ansible
#          ansible-playbook install-front-docker.yml
#       EOF

#    }
#     }