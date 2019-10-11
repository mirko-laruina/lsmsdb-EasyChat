import React, {Component} from 'react';
import {ListGroup, List} from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import axios from 'axios';

class ChatMessages extends Component {

  constructor(){
    super()
    this.state = {
      messageList: [],
    };
  }

  componentDidUpdate(prevPops){
    if(this.props.chatId != prevPops.chatId)
      this.getMessages(this.props.chatId, this.props.sid);
  }

  getMessages(chat, sid){
    var self = this
    axios.post('http://localhost:8080/api/v1/chat/'+chat+'/messages', null,{ params: {
      sessionId: sid,
    }})
    .then(function (response) {
      self.setState({
        messageList: response.data,
      })
    })
    .catch(function (error) {
      console.log(error);
    });
  }

  render() {
    return (
      <div className="chatMessages">
          {
            this.state.messageList.length === 0 ? (
            "Nothing to show"
          ) : (
            <ListGroup>
              {this.state.messageList.map((message, i) =>
                <ListGroup.Item key={i}>{message.text}</ListGroup.Item>
              )}
            </ListGroup>
          )}
      </div>
    );
  }
}

export default ChatMessages;
