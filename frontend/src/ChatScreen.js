import React, {Component} from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import './chat.css';
import ChatsList from './ChatsList.js'
import ChatMessages from './ChatMessages.js'
import ChatSend from './ChatSend.js'
import {Container, Row, Col} from 'react-bootstrap';
import axios from 'axios';
import Cookies from 'universal-cookie';
const cookies = new Cookies();

class ChatScreen extends Component {

  constructor(){
    super()
    this.state = {
      chatId: 0,
    }

    this.username = cookies.get('username');
    this.setChat = this.setChat.bind(this)
  }

  componentDidMount(){
    this.isLogged();
  }

  isLogged(){
    axios.post('http://localhost:8080/api/v1/auth/check', null,{ params: {
      sessionId: this.props.sid,
    }})
    .then(function (response) {
        if(!response['data']['success']){
          cookies.remove('sessionId');
          window.location.reload();
        }
    })
    .catch(function (error) {
      console.log(error);
    });  
  }

  setChat(id){
    this.setState({
      chatId: id,
    })
  }

  render() {
    return (
      <div className="chat border">
        <Container>
          <Row>
            <Col md={4}>
              <ChatsList className="chatsList"  username={this.username} sid={this.props.sid} setChat={this.setChat}/>
            </Col>
            <Col md={8}>
                <ChatMessages username={this.username} sid={this.props.sid} chatId={this.state.chatId}/>
                <ChatSend sid={this.props.sid} chatId={this.state.chatId}/>
            </Col>
          </Row>
        </Container>
      </div>
    );
  }
}

export default ChatScreen;
