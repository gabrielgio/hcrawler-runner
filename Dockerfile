FROM clojure:openjdk-14-tools-deps-slim-buster

WORKDIR /app

COPY . .

ENTRYPOINT clojure -m hcrawler-runner.main