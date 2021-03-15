# Using Traefik as Load Balancer

```
docker compose up -d --scale fredbet=2
```

Edit your /etc/hosts file and add the following:

```
127.0.0.1       fredbet.example.com
127.0.0.1       monitor.example.com
```

After that you can call the frebet application at http://fredbet.example.com
