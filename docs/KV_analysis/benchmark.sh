user1=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 8)
user2=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 8)
user3=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 8)
echo $user1
echo $user2
echo $user3

echo "Registering random users"
curl --request POST --url 'localhost:8080/api/v1/users' \
                           -d "{ \"username\": \"$user1\", \"password\": \"$user1\" }" \
                           -H 'Content-Type: application/json'
echo ''
curl --request POST --url 'localhost:8080/api/v1/users' \
                           -d "{ \"username\": \"$user2\", \"password\": \"$user2\" }" \
                           -H 'Content-Type: application/json' -s
echo ''
curl --request POST --url 'localhost:8080/api/v1/users' \
                           -d "{ \"username\": \"$user3\", \"password\": \"$user3\" }" \
                           -H 'Content-Type: application/json' -s
echo ''
echo "Logging users"
sid1=$(curl --request POST --url 'localhost:8080/api/v1/auth/login' \
                    -d "{ \"username\": \"$user1\", \"password\": \"$user1\" }" \
                    -H 'Content-Type: application/json' \
                    -s | cut -d '"' -f 4)
sid2=$(curl --request POST --url 'localhost:8080/api/v1/auth/login' \
                    -d "{ \"username\": \"$user2\", \"password\": \"$user2\" }" \
                    -H 'Content-Type: application/json' \
                    -s | cut -d '"' -f 4)
sid3=$(curl --request POST --url 'localhost:8080/api/v1/auth/login' \
                    -d "{ \"username\": \"$user3\", \"password\": \"$user3\" }" \
                    -H 'Content-Type: application/json' \
                    -s | cut -d '"' -f 4)
echo "Creating chats"
curl --request POST --url "localhost:8080/api/v1/chats?sessionId=$sid1" \
                    -d "{ \"name\": \"null\", \"members\": [\"$user2\"] }" \
                    -H 'Content-Type: application/json' \
                    -s
echo ''
curl --request POST --url "localhost:8080/api/v1/chats?sessionId=$sid1" \
                    -d "{ \"name\": \"null\", \"members\": [\"$user3\"] }" \
                    -H 'Content-Type: application/json' \
                    -s
echo ''
curl --request POST --url "localhost:8080/api/v1/chats?sessionId=$sid1" \
                    -d "{ \"name\": \"Benchmark\", \"members\": [\"$user2\", \"$user3\"] }" \
                    -H 'Content-Type: application/json' \
                    -s
echo ''
echo "Getting chat"
chat1=$(curl --request GET --url "localhost:8080/api/v1/chats?sessionId=$sid1" \
                    -s | jq -r '.[0] | .chatId')
chat2=$(curl --request GET --url "localhost:8080/api/v1/chats?sessionId=$sid1" \
                    -s | jq -r '.[0] | .chatId')
chat3=$(curl --request GET --url "localhost:8080/api/v1/chats?sessionId=$sid1" \
                    -s | jq -r '.[0] | .chatId')

echo "Starting benchmark on chat list API"
siege -c 1 -r 5000 -H 'Content-Type: application/json' \
                "http://localhost:8080/api/v1/chats?sessionId=$sid1" -b | awk 'END{printf "%s",$1}' && echo ''
siege -c 1 -r 5000 -H 'Content-Type: application/json' \
                "http://localhost:8080/api/v1/chats?sessionId=$sid2" -b | awk 'END{printf "%s",$1}' && echo ''
siege -c 1 -r 5000 -H 'Content-Type: application/json' \
                "http://localhost:8080/api/v1/chats?sessionId=$sid3" -b | awk 'END{printf "%s",$1}' && echo ''

echo "Benchmark sending messages"
siege -c 1 -r 5000 -H 'Content-Type: application/json' \
                "http://localhost:8080/api/v1/chat/$chat1/messages?sessionId=$sid1 POST {\"text\": \"Ciao\" }"  -b | awk 'END{printf "%s",$1}' && echo ''
siege -c 1 -r 5000 -H 'Content-Type: application/json' \
                "http://localhost:8080/api/v1/chat/$chat2/messages?sessionId=$sid2 POST {\"text\": \"Ciao\" }" -b | awk 'END{printf "%s",$1}' && echo ''
siege -c 1 -r 5000 -H 'Content-Type: application/json' \
                "http://localhost:8080/api/v1/chat/$chat3/messages?sessionId=$sid3 POST {\"text\": \"Ciao\" }"  -b | awk 'END{printf "%s",$1}' && echo ''

echo "Benchmark reading all messages from chat"
siege -c 1 -r 200 -H 'Content-Type: application/json' \
                "http://localhost:8080/api/v1/chat/$chat1/messages?sessionId=$sid1" -b | awk 'END{printf "%s",$1}' && echo ''