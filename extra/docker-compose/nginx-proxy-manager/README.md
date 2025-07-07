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

You have to add a proxy host. Here is an example for testing it locally: 

    Domain Name: localhost (in real scenario use the domain name you have configured)
    Scheme: http
    Forwarded Hostname: fredbet
    Forwarded Port: 8080

You can reach `FredBet` with http://localhost. 
