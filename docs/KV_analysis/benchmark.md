# Benchmark

## Aim and requirements (???)
An evaluation of the LevelDB implementation has been conducted using a custom made shell script (`benchmark.sh`).
The script relies on the linux commands `curl` and `siege`, other than the POSIX `echo` and `awk`. Its aim is to call the server API repeatedly like users would.
In a first phase users and chats are created, the `api/v1/chats` (getting chat list) is then tested, followed by `api/v1/chat/{chatId}/messages` (POST method: posting new messages) and `api/v1/chat/{chatId}/messages` (GET method: getting all the messages).
A more interesting query would be requesting a certain range of messages but, since an API change was needed between the MySQL and LevelDB implementation, in particulare from `timestamp` to `messageId` as range delimiters, this cannot be easily done.
Every API is called multiple times by `siege`. The first two API are tested against 3 different chats, the last (since it is more time expensive) only with a single chat.


## Results
Results are as follow:
| Database type | Get chat list | Write message | Get messages |
| MySQL         | 470 517 498   | 159 148 658   | 11.93        |
| LevelDB       | 687 1144 1204 | 902 1302 1298 | 4.02         |

LevelDB performes a lot better in the execution of the first two APIs, but it is worst (3x) in getting all the messages.
This could seem to be a major disadvantage, but it has to be noted that the entire message list is requested once per page load. All the subsequent requests will be ranged from the last message received by the user to the last message sent to that chat (usually the response will contain zero or few messages).

## Possible improvement
Other than the usage of long polling, from which would benefit both the implementations, it has to be noted that a different LevelDB implementation for message storing could be used.
As of now, three read operations have to be made for every message since `text`, `timestamp` and `sender` field are stored separately. While this is optimal from a logical point of view, the server load would benefit from a unique field storing the aggregation of the three.

```Java
String val = asString(levelDBStore.get(bytes((String.format("chat:%d:message:%d:text", chatId, i)))))
String timestamp = asString(levelDBStore.get(bytes(String.format("chat:%d:message:%d:timestamp", chatId, i))))
Long sender = bytesToLong(levelDBStore.get(bytes(String.format("chat:%d:message:%d:sender", chatId, i))));
```

would become

```Java
SerializedData data = new SerializedData(levelDBStore.get(bytes((String.format("chat:%d:message:%d:text", chatId, i))))
```

where `SerializedData` is a class capable of serializing and deserializing the stored data into the three previous fields.