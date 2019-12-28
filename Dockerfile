FROM gabrielgio/clojure

WORKDIR /app

COPY . .

ENTRYPOINT clojure -m hcrawler-runner.main