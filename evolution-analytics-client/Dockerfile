FROM node:12.4.0-alpine

# Create Directory for the Container
WORKDIR /usr/src/app

RUN ls

# Copy evolution-model to the work directory
COPY evolution-analytics-model ./evolution-analytics-model

# Only copy the package.json file to work directory
COPY package.json .

# Install angular-cli dependency to build the project
RUN npm install -g @angular/cli

# Install all the dependencies
RUN npm install

# Copy all other source code to work directory
ADD . /usr/src/app

# Build the Angular application
RUN npm run build

# Start the application
CMD ["ng","serve","--host", "0.0.0.0"]
EXPOSE 5000
