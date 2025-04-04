FROM node:18-alpine
WORKDIR /app

ARG PORT

COPY /frontend/package*.json ./
RUN npm install

COPY /frontend .

EXPOSE ${PORT}
CMD ["npm", "start"]