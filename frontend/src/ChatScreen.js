import React, {Component} from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import './chat.css';
import ChatsList from './ChatsList.js'
import ChatMessages from './ChatMessages.js'
import axios from 'axios';
import Cookies from 'universal-cookie';
const cookies = new Cookies();

class ChatScreen extends Component {

  constructor(){
    super()
    this.state = {
      chatId: 0,
    }

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
      <div className="chat">
        <div className="row">
            <div className="col-md-4"><ChatsList sid={this.props.sid} setChat={this.setChat}/></div>
            <div className="col-md-8">
                <ChatMessages sid={this.props.sid} chatId={this.state.chatId}/>
            </div>
        </div>
      </div>
    );
  }
}

export default ChatScreen;
