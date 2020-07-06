FROM node:12.4.0-alpine

# Create Directory for the Container
WORKDIR /usr/src/app

# SETUP env
# add `/app/node_modules/.bin` to $PATH
ENV PATH /app/node_modules/.bin:$PATH
RUN apk --no-cache add git
RUN apk add --no-cache libc6-compat
RUN npm config set unsafe-perm true

# Only copy the package.json file to work directory
COPY ["package.json", "."]
# Copy evolution-model to the work directory
COPY ["evolution-analytics-model", "."]
# Install all Packages
RUN npm install && npm install -g @angular/cli@9.0.7

# Copy all other source code to work directory
ADD . /usr/src/app
RUN npm run build

# start app
CMD ["ng","serve","--host", "0.0.0.0"]
EXPOSE 5000
