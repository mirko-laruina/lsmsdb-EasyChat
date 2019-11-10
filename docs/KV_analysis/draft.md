# SQL->KV Analysis

## Naive conversion
| user:$userId:username | Users.username |
| user:$userId:password | Users.password |
| session:$sessionId:expiry | Sessions.expiry |
| session:$sessionId:userId | Sessions.userId |
| users:nextId | Next id for a user |
| chat:$chatId:name | Chats.name |
| chat:$chatId:lastActivity | Chats.lastActivity |
| chat:$chatId:members | List of chat members |
| chat:$chatId:admin | Chats.adminId|
| chat:$chatId:messages:nextId | Next id for a chat |
| chat:$chatId:message:$messageId:text | Messages.text |
| chat:$chatId:message:$messageId:timestamp | Messages.timestamp |
| chat:$chatId:message:$messageId:sender | Messages.senderUserId |
| chats:nextId | Next id for a chat |

Note that a list of chat messages is not required since the messages cannot
be deleted and therefore by using an auto-incremental integer id, all 
ids from 0 to nextId-1 will be present.

## Use-cases/Methods analysis
Since mapping between use-cases and DB-abstraction-layer methods is 
straightforward, this analysis will take into consideration the 
methods.

```java
/**
* Returns the list of chats for the user identified by the given userId.
*
* @return the list of chats or null in case of error.
*/
List<Chat> getChats(long userId);
```
Since one of the use-cases is retrieving the chats of one user, the naive
conversion is not efficient since a linear search is required.
Therefore, we should add a new list to map user chats, i.e.:

| user:$userId:chats | List of chat ids where the user is present |

```java
/**
* Returns the list of members for the chat identified by the given chatId.
*
* @return the list of members or null in case of error.
*/
List<User> getChatMembers(long chatId);
```
This method can be trivially implemented using the naive implementation.

```java
/**
* Adds the user identified by the given userId to the chat identified by the given chatId.
*
* @return {@code true} in case of success, {@code false} otherwise.
*/
boolean addChatMember(long chatId, long userId);
```
This method can be trivially implemented using the naive implementation.

```java
/**
* Removes the user identified by the given userId from the chat identified by the given chatId.
*
* @return {@code true} in case of success, {@code false} otherwise.
*/
boolean removeChatMember(long chatId, long userId);
```
This method can be trivially implemented using the naive implementation.

```java
/**
* Checks whether the user identified by the given userId is inside the chat identified by the given chatId.
*
* @return {@code true} if the user is a member of the cat, {@code false} otherwise or in case of error.
*/
boolean checkChatMember(long chatId, long userId);
```
This method can be trivially implemented using the naive implementation. 
The only design choice would be choosing whether to match the user in the
member list of the chat or viceversa.

```java
/**
* Adds the given message to the appropriate chat.
*
* The chat is retrieved from the {@link Message#chat} field, whose {@link Chat#chatId} must be set.
*
* @return the id of the added message.
*/
long addChatMessage(Message message);
```
This method can be trivially implemented using the naive implementation.

```java
/**
* Creates a new chat with the given name, admin and members.
*
* The admin is always added as member, even if not present in the provided member list.
*
* @return the id of the created chat or -1 in case of error.
*/
long createChat(String name, long adminId, List<Long> userIds);
```
This method can be trivially implemented using the naive implementation.

```java
/**
* Deletes the chat with the given chatId.
*
* @return {@code true} in case of success, {@code false} otherwise.
*/
boolean deleteChat(long chatId);
```
This method can be trivially implemented using the naive implementation.

```java
/**
* Returns the chat identified by the given chatId.
*
* @return the chat or null in case of error.
*/
Chat getChat(long chatId);
```
This method can be trivially implemented using the naive implementation.

```java
/**
* Registers a new user.
*
* @return the id of the created chat or -1 in case of error.
*/
long createUser(User user);
```
This method can be trivially implemented using the naive implementation.


```java
/**
* Returns user identified by the given username.
*
* @return the user in case of success, null otherwise.
*/
User getUser(String username);
```
This method is inefficient using the proposed naive implementation. 
The use of a reverse index would make it more efficient, i.e.:

| username:$username:userId | Users.id |

```java
/**
* Returns userId of the user who owns the session identified by the given sessionId.
*
* In case the session is expired, the session is deleted from the db and the result is the same
* as if no session were present.
*
* @return the id of the user in case of success, -1 otherwise.
*/
long getUserFromSession(String sessionId);
```
This method can be trivially implemented using the naive implementation.

```java
/**
* Creates a new session.
*
* @return {@code true} in case of success, {@code false} otherwise.
*/
boolean setUserSession(UserSession user);
```
This method can be trivially implemented using the naive implementation.


```java
/**
* Removes the session identified by the given sessionId.
*
* @return {@code true} in case of success, {@code false} otherwise.
*/
boolean removeSession(String sessionId);
```
This method can be trivially implemented using the naive implementation.

```java
/**
* Returns true if there exists a private chat between user1 and user2.
*
* NB: a private chat is a chat with only two members.
*
* @return {@code true} if it exists, {@code false} otherwise or in case of errors.
*/
boolean existsChat(long user1, long user2);
```
This method can be implemented in an inefficent way iterating through the 
user's chats and its members. A more efficient way would be inserting a
redundant list of private chats the user is in.

| user:$userId:privateChats | List of user ids the user has a private chat with |

However, for a proof-of-concept implementation, this redundancy is 
not required.

```java
/**
* Returns a list of messages for the given chat, in the given time range, up to the given
* number of elements, sorted in ascending message sending time.
*
* @param chatId id of the chat whose messages are to be retrieved
* @param from start of the time range (included). It can be null, whose meaning is that there is no lower bound.
* @param to end of the time range (excluded). It can be null, whose meaning is that there is no upper bound.
* @param n maximum number of elements to return. If from is not null, messages are counted from {@code from} up
*          to n or {@code to}. If from is null, messages are counted from {@code to} up to n or {@code from}.
* @return the list of messages or null in case of error.
*/
List<Message> getChatMessages(long chatId, Instant from, Instant to, int n);
```
Making such a range query is very inefficient in a key-value database.
However, this API is only used to lazy load chat messages and receive
new messages, thus it can be simplified. 
In particular, the client asks a certain number of messages before 
a certain message or all messages after a given message. 
In order to reference the message, the timestamp is used. This was
done for the sake of generality, since it is not much more expensive 
in a SQL database.
In the case of a key-value database, we would need to change this API
by using the message ids to mark the bounds.
By doing so, since messages cannot be deleted and therefore their ids 
are all and every the numbers within 0 and nextId-1, the query becomes trivial.

An alternative would have been changing the id generation, using timestamp
but collisions would have been a problem.

## Final design
| user:$userId:username | Users.username |
| user:$userId:password | Users.password |
| user:$userId:chats | List of chat ids where the user is present |
| username:$username:userId | Users.id |
| session:$sessionId:expiry | Sessions.expiry |
| session:$sessionId:userId | Sessions.userId |
| users:nextId | Next id for a user |
| chat:$chatId:name | Chats.name |
| chat:$chatId:lastActivity | Chats.lastActivity |
| chat:$chatId:members | List of chat members |
| chat:$chatId:admin | Chats.adminId|
| chat:$chatId:messages:nextId | Next id for a chat |
| chat:$chatId:message:$messageId:text | Messages.text |
| chat:$chatId:message:$messageId:timestamp | Messages.timestamp |
| chat:$chatId:message:$messageId:sender | Messages.senderUserId |
| chats:nextId | Next id for a chat |

Plus one API change:

```java
/**
* Returns a list of messages for the given chat, in the given time range, up to the given
* number of elements, sorted in ascending message sending time.
*
* @param chatId id of the chat whose messages are to be retrieved
* @param fromId start of the id range (included). A value of -1 means that there is no lower bound.
* @param toId end of the time range (excluded). A value of -1 means that there is no upper bound.
* @param n maximum number of elements to return. If from is not -1, messages are counted from {@code from} up
*          to n or {@code to}. If from is -1, messages are counted from {@code to} up to n or {@code from}.
* @return the list of messages or null in case of error.
*/
List<Message> getChatMessages(long chatId, long fromId, long toId, int n);
```