FROM node:12.4.0-alpine

# Create Directory for the Container
WORKDIR /usr/src/app

RUN ls

# Only copy the package.json file to work directory
COPY package.json .
# Copy evolution-model to the work directory
COPY evolution-analytics-model ./

# Install angular-cli dependency to build the project
RUN npm install -g @angular/cli

RUN npm cache clean --force
# Install all the dependencies
#RUN npm install @angular-builders/custom-webpack && npm install @angular-builders/dev-server && npm install @angular-devkit/build-angular && npm install @angular/cli && npm install @angular/compiler-cli && npm install @angular/language-service
RUN npm install --force

# Copy all other source code to work directory
ADD . /usr/src/app

# Build the Angular application
RUN npm run build

# Start the application
CMD ["ng","serve","--host", "0.0.0.0"]
EXPOSE 5000
