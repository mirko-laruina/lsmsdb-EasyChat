import React, {Component} from 'react';
import { ButtonGroup, Button } from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.min.css';
import axios from 'axios';

class ChatList extends Component {

  constructor(){
    super();
    this.state = {
      chatList: [],
    }
    this.selectedChat = -1;
  }

  componentDidMount(){
    this.getChat();
  }

  getChat(){
    var self = this;
    axios.post('http://localhost:8080/api/v1/chats', null,{ params: {
      sessionId: this.props.sid,
    }})
    .then(function (response) {
        response.data.forEach((chat) => {chat.variant = "light"})
        self.setState({
          chatList: response.data,
        })
        console.log(self.state.chatList)
    })
    .catch(function (error) {
      console.log(error);
    }); 
  }

  changeChat(i){
    let newState = Object.assign({}, this.state);
    newState.chatList[i].variant = "warning";
    if(this.selectedChat != -1)
      newState.chatList[this.selectedChat].variant = "light";
    this.selectedChat = i;
    this.setState(newState);
  }

  render() {
    return (
      <div className="chatsList">
        <ButtonGroup vertical>
          {
            this.state.chatList.map((chat, i) => {
              return <Button className="chatLabel border"
                              variant={chat.variant}
                              key={chat.chatId}
                              onClick={() => this.changeChat(i)}
                              >
                        {chat.name}
                      </Button>
            })
          }
        </ButtonGroup>
      </div>
    );
  }
}

export default ChatList;
