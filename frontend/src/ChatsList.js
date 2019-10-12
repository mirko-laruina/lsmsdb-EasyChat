import React, {Component} from 'react';
import { ButtonGroup, Button, InputGroup } from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.min.css';
import axios from 'axios';
import CreateModal from './CreateModal.js'

class ChatList extends Component {

  constructor(){
    super();
    this.state = {
      chatList: [],
      showCreate: false,
    }
    this.selectedChat = -1;
    this.handleShowCreate = this.handleShowCreate.bind(this)
  }

  componentDidMount(){
    this.getChat();
  }

  getChat(){
    var self = this;
    axios.get('http://localhost:8080/api/v1/chats',{ params: {
      sessionId: this.props.sid,
    }})
    .then(function (response) {
        response.data.forEach((chat) => {chat.variant = "light"})
        self.setState({
          chatList: response.data,
        })
    })
    .catch(function (error) {
      console.log(error);
    }); 
  }

  changeChat(chatId, i){
    let newState = Object.assign({}, this.state);
    if(this.selectedChat !== -1)
      newState.chatList[this.selectedChat].variant = "light";
    newState.chatList[i].variant = "warning";
    this.selectedChat = i;
    this.setState(newState);
    this.props.setChat(chatId);
  }

  handleShowCreate(){
    let newState = Object.assign({}, this.state);
    newState.showCreate = !newState.showCreate;
    this.setState(newState);
  }

  render() {
    return (
      <div className="chatsList">
        <ButtonGroup vertical> 
          <Button className="chatLabel border addChat"
                  variant="outline-success"
                  onClick={this.handleShowCreate}>+ Create
          </Button>
        </ButtonGroup>
          {
            this.state.chatList.map((chat, i) => {
                if(chat.members.length === 2){
                  return <Button className="chatLabel border"
                        variant={chat.variant}
                        key={chat.chatId}
                        onClick={() => this.changeChat(chat.chatId, i)}>
                             {
                               chat.members.map((member) =>
                                  member.username !== this.props.username ? (
                                    member.username
                                  ) : null
                              )}
                        </Button>
                } else {
                  return (
                    <InputGroup className="border" key={chat.chatId}>
                        <Button variant={chat.variant}
                                className="groupLabel"
                                onClick={() => this.changeChat(chat.chatId, i)}>
                                  {chat.name}
                        </Button>
                      <InputGroup.Append className="groupLabelManage">
                        <Button variant={chat.variant} disabled={!chat.isAdmin}>
                                  +
                        </Button>
                      </InputGroup.Append>
                    </InputGroup>
                  )
                }
            })
          }

        <CreateModal handler={this.handleShowCreate} show={this.state.showCreate} />
      </div>
    );
  }
}

export default ChatList;
