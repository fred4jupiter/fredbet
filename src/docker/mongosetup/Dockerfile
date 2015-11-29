FROM mongo:3.0.6

MAINTAINER Michael Staehler <hamsterhase@gmx.de>

RUN echo 'Creating mongoDB replica set set...'

COPY ./setup.sh /setup.sh

ENTRYPOINT ["/setup.sh"]
