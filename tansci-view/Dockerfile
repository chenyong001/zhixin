FROM node:16.17-alpine as build-stage
# FROM node:14.18.1-alpine as build-stage
# build stage
RUN apk -U upgrade

WORKDIR /app

COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# production stage
FROM nginx:stable-alpine as production-stage

COPY --from=build-stage /app/dist/ /usr/share/nginx/html
# COPY dist/ /usr/share/nginx/html
COPY default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]


