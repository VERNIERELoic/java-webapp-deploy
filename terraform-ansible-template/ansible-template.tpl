[front_group]
%{ for element in front ~}
${element.name} ansible_host=${element.access_ip_v4}
%{ endfor ~}


[back_group]
%{ for element in back ~}
${element.name} ansible_host=${element.access_ip_v4}
%{ endfor ~}

[others]
%{ for element in traefik ~}
${element.name} ansible_host=${element.access_ip_v4}
%{ endfor ~}
%{ for element in bdd ~}
${element.name} ansible_host=${element.access_ip_v4}
%{ endfor ~}