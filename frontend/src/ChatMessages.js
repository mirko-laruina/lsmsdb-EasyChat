import React, {Component, createRef} from 'react';
import {ListGroup, Card, Alert} from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import axios from 'axios';

class ChatMessages extends Component {

  constructor(){
    super()
    this.state = {
      messageList: [],
    };
    this.ref = createRef();
  }

  componentDidUpdate(prevPops){
    if(this.props.chatId !== prevPops.chatId){
      this.getMessages(this.props.chatId, this.props.sid);
    }
    this.ref.current.scrollTop = this.ref.current.scrollHeight;
  }

  getMessages(chat, sid){
    var self = this
    axios.post('http://localhost:8080/api/v1/chat/'+chat+'/messages', null,{ params: {
      sessionId: sid,
    }})
    .then(function (response) {
      self.setState({
        messageList: response.data.reverse(),
      })
    })
    .catch(function (error) {
      console.log(error);
    });
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
                  <div key={i}><Card bg="info" className="message">{message.text}<p className="text-right">{message.timestamp}</p></Card>
                  <p className="text-left">{message.sender.username}</p></div>
                ) : (
                  <div key={i}><Card bg="success" className="message">{message.text}<p className="text-right">{message.timestamp}</p></Card>
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
