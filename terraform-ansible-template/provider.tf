terraform {
  required_providers {
    openstack = {
      source = "terraform-provider-openstack/openstack"
      version = "1.33.0"
    }
  }
}

provider "openstack" {
  auth_url = "https://auth.cloud.ovh.net/v3.0/"
  domain_name = "default" # Nom de domaine - Toujours à "default" pour OVH
  alias = "ovh"
}

