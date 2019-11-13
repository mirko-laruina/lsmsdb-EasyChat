import React, {Component, createRef} from 'react';
import {ListGroup, Card, Alert} from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import axios from 'axios';
const API_URL = 'http://'+window.location.hostname+':8080/api/v1/'

class ChatMessages extends Component {

  constructor(){
    super()
    this.state = {
      messageList: [],
    };
    this.ref = createRef();
    this.iid = 0;
    this.lastTimeRequested = -1;
    this.isWaiting = false;
    this.firstRequest = true
  }

  componentDidUpdate(prevPops){
    if(this.props.chatId !== prevPops.chatId){
      this.setState({
        messageList: [],
      });
      this.getMessages(this.props.chatId, this.props.sid, -1, -1, 0);
    }
    if(prevPops.chatId === 0)
      this.ref.current.scrollTop = this.ref.current.scrollHeight;
  }

  getMessages(chat, sid, from, to, n){
    if(!this.isWaiting || this.lastTimeRequested !== from){
      this.isWaiting = true;
      this.lastTimeRequested = from;
      var self = this
      axios.get(API_URL+'chat/'+chat+'/messages',{ params: {
        sessionId: sid,
        from: from,
        to: to,
        n: n
      }})
      .then(function (response) {
        //TODO: check success
        var newMessageList = self.state.messageList
        response.data.messages.map((message) => {
            newMessageList[message.messageId] = message
        })
        self.setState({
          messageList: newMessageList,
        })
        if(response.data.messages.length > 0){
          self.ref.current.scrollTop = self.ref.current.scrollHeight;
        }
        self.isWaiting = false;
        if(self.props.chatId == chat){
            self.getMessages(chat, sid, self.state.messageList.length, -1, 0)
        }
      })
      .catch(function (error) {
        console.log(error);
      });
    }
  }

  UTC2LocalizedString(utc){
    return new Date(utc).toLocaleString();
  }

  render() {
    return (
      <div className="chatMessages" ref={this.ref}>
          {
            this.state.messageList.length === 0 ? (
            <Alert variant="light" className="empty-chat"> Nothing to show: select a chat to start</Alert>
          ) : (
            <ListGroup  className="messageList">
              {this.state.messageList.map((message, i) =>
                this.props.username !== message.sender.username ? (
                  <div key={i}><Card bg="info" className="message">{message.text}<p className="text-right timestamp">{this.UTC2LocalizedString(message.timestamp)}</p></Card>
                  <p className="text-left">{message.sender.username}</p></div>
                ) : (
                  <div key={i}><Card bg="success" className="message"><p className="text-right">{message.text}</p><p className="text-right timestamp">{this.UTC2LocalizedString(message.timestamp)}</p></Card>
                  <p className="text-right">You</p></div>
                )
              )}
            </ListGroup>
          )}
      </div>
    );
  }
}

export default ChatMessages;
