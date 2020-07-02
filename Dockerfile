# base image
FROM node:12.4.0-alpine

RUN mkdir -p /src

# add `/app/node_modules/.bin` to $PATH
ENV PATH /app/node_modules/.bin:$PATH

RUN apk --no-cache add git

RUN apk add --no-cache libc6-compat

RUN npm config set unsafe-perm true

# install and cache app dependencies
COPY ["./evolution-analytics-client","/src/evolution-analytics-client"]
COPY ["./evolution-analytics-model","/src/evolution-analytics-model"]

WORKDIR /src/evolution-analytics-client

RUN npm install && npm install -g @angular/cli@9.0.7

RUN npm run build

# start app
CMD ["ng","serve","--host", "0.0.0.0"]