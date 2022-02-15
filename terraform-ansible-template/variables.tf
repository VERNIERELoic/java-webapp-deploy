variable "powerstate" {
        description     = "power state of the vm shutoff or active "   
        default         = "active"
}

variable "ubuntu_image" {
        default = {
            image_id    = "....."
            image_name  = "OS_ubuntu....."
        }
}