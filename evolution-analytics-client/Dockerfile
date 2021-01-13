FROM node:12.4.0-alpine as builder

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


# Runtime
FROM nginx
COPY docker/proxy.conf /etc/nginx/conf.d/proxy.conf
COPY docker/default.conf.template /etc/nginx/templates/default.conf.template
COPY docker/proxy_params.conf /etc/nginx/snippets/proxy_params.conf
COPY --from=builder /usr/src/app/dist/evolution-analytics-client /usr/share/nginx/html
ENV API_HOST=evolution-backend
ENV API_PORT=3000