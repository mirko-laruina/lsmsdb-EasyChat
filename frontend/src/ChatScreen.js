import React, {Component} from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import './chat.css';
import ChatsList from './ChatsList.js'
import ChatMessages from './ChatMessages.js'
import axios from 'axios';
import Cookies from 'universal-cookie';
const cookies = new Cookies();

class ChatScreen extends Component {

  componentDidMount(){
    this.isLogged();
    console.log(this.props.sid)
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

  render() {
    return (
      <div className="chat">
        <div className="row">
            <div className="col-md-4"><ChatsList sid={this.props.sid}/></div>
            <div className="col-md-8">
                <ChatMessages />
            </div>
        </div>
      </div>
    );
  }
}

export default ChatScreen;
