# Automatization TP


# Prerequis

## Terraform

* Il est recommandé d'utiliser la dernière version de l'outil Terraform.

## Ansible

* L'installation de la dernière version est recommandée (minimum 2.9 d'Ansible !!).
  * Vous **devez** installer la dépendance suivante pour utiliser le module docker:

```bash
ansible-galaxy collection install community.docker
```
# Utilisation

## Terraform

1. Pour commencer, initier le projet `terraform init`

2. Sourcez le fichier de paramètre **Openstack** qui vous a été fourni pour mettre en place les bonnes variables d'environements dans __votre shell courant__ uniquement

3. Avec la commande `terraform plan`, analyser les changements qui vont être réalisés.

4. Pour appliquer les changements `terraform apply`.

5. Pour détruire tous vos composants, utiliser la commande `terraform destroy`.

6. Si vous travaillez à plusieurs, vous pouvez mettre à jour le status de votre Terraform avec la commande `terraform refresh`

## Ansible

Le ***rootdir*** d'Ansible se situe dans le répertoire `./ansible/`. Se mettre dans le répertoire avant de jouer une commande `ansible`.

1. L'inventory est automatiquement populé à la fin de la commande `terraform apply`
* Il se trouve dans le fichier `./inventory/hosts`

2. Pour tester l'accès à vos machines, utiliser `ansible all -m ping`
* Cela permettra de valider que votre configuration de clés SSH est correcte.

3. Pour jouer un `playbook`, utiliser la commande `ansible-playbook install-nginx-with-docker.yml`

## Warning

Ce repository/projet **ne fonctionne pas correctement*.

En effet, il manque un élément pour permettre l'accès au service Nginx depuis l'INTERNET !  
Au niveau de l'instance, il n'y as pas de problème. Si vous jouez la commande suivante sur l'instance, vous devriez voir que tout fonctionne : `curl -v http://localhost:80`

## Lien Module Ansible

https://docs.ansible.com/ansible/latest/collections/community/docker/index.html / https://docs.ansible.com/ansible/latest/collections/community/general/docker_container_module.html

https://docs.ansible.com/ansible/latest/collections/ansible/builtin/apt_module.html

https://docs.ansible.com/ansible/latest/collections/ansible/builtin/apt_key_module.html

https://docs.ansible.com/ansible/latest/collections/ansible/builtin/pip_module.html

## Lien Terraform Utile

https://registry.terraform.io/providers/terraform-provider-openstack/openstack/latest/docs

https://registry.terraform.io/providers/terraform-provider-openstack/openstack/latest/docs/resources/compute_instance_v2

https://registry.terraform.io/providers/terraform-provider-openstack/openstack/latest/docs/resources/compute_keypair_v2

https://registry.terraform.io/providers/terraform-provider-openstack/openstack/latest/docs/resources/networking_secgroup_v2

https://registry.terraform.io/providers/terraform-provider-openstack/openstack/latest/docs/resources/networking_secgroup_rule_v2


# N'oubliez pas de commit tous les fichiers ! 
