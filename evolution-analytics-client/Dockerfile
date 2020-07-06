FROM node:12.4.0-alpine

# Create Directory for the Container
WORKDIR /usr/src/app

RUN ls -a

# Only copy the package.json file to work directory
COPY ["package.json", "."]
# Copy evolution-model to the work directory
COPY ["evolution-analytics-model", "."]
# Clean node_modules and package-lock
RUN rm -rf node_modules && rm package-lock.json
# Clean the cache
RUN npm cache clean --force
# Install all Packages
##RUN npm install -g @angular/cli@9.0.7
#RUN npm install @angular-builders/custom-webpack && npm install @angular-builders/dev-server && npm install @angular-devkit/build-angular && npm install @angular/cli && npm install @angular/compiler-cli && npm install @angular/language-service
RUN npm install

# Copy all other source code to work directory
ADD . /usr/src/app
RUN npm run build

# start app
CMD ["ng","serve","--host", "0.0.0.0"]
EXPOSE 5000
