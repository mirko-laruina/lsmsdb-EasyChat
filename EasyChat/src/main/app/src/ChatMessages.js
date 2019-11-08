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
  }

  componentDidUpdate(prevPops){
    if(this.props.chatId !== prevPops.chatId){
      this.setState({
        messageList: [],
      });
      this.stopRefresh();
      this.getMessages(this.props.chatId, this.props.sid, 0, 0, 0);
      this.startRefresh(this.props.chatId, this.props.sid);
    }
    if(prevPops.chatId === 0)
      this.ref.current.scrollTop = this.ref.current.scrollHeight;
  }

  stopRefresh(){
    window.clearInterval(this.iid);
  }

  startRefresh(chat, sid){
    var self = this;
    this.iid = window.setInterval(function(){
        var lastMsgId = 0;
        if(self.state.messageList.length > 0){
            lastMsgId = self.state.messageList[self.state.messageList.length-1].messageId;
        }
        self.getMessages(chat, sid, lastMsgId, -1, 0)
    }, 500);

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
        self.setState({
          messageList: self.state.messageList.concat(response.data.messages),
        })
        if(response.data.messages.length > 0){
          self.ref.current.scrollTop = self.ref.current.scrollHeight;
        }
        self.isWaiting = false;
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
