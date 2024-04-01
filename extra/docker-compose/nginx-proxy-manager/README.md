# Docker Compose Example using Ngnix Proxy Manager

This example uses the (https://github.com/NginxProxyManager/nginx-proxy-manager)[Nginx Proxy Manager].


## Prepare Steps

Copy `.env.template` to `.env`. Ajust the values to your needs.

## Start Containers

    docker compose up -d

## Configure Ngnix Proxy Manager

### First Login

You can access the UI via (http://localhost:81) and then you have to change your password. 

Default admin user:

    Email:    admin@example.com
    Password: changeme

### Add Proxy Host

You have to add a proxy host. Use `http://fredbet:8080` for the destination.



    