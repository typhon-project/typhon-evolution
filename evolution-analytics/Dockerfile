FROM node:10.13.0-alpine

# Create Directory for the Container
WORKDIR /usr/src/app
# Only copy the package.json file to work directory
COPY package.json .
# Copy evolution-moden to the work directory
COPY evolution-analytics-model ./
# Install all Packages
RUN npm install
# Install ts package
RUN npm install -g typescript
RUN npm install copyfiles -g
# Copy all other source code to work directory
ADD . /usr/src/app
# TypeScript
RUN tsc
# Start
CMD [ "npm", "run", "start" ]
EXPOSE 3000