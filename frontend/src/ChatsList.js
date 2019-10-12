import React, {Component} from 'react';
import { ButtonGroup, Button, InputGroup } from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.min.css';
import axios from 'axios';
import CreateModal from './CreateModal.js'
import ManageModal from './ManageModal.js'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faTools } from '@fortawesome/free-solid-svg-icons'


class ChatList extends Component {

  constructor(){
    super();
    this.state = {
      chatList: [],
      showCreate: false,
      showManage: false
    }
    this.selectedChat = -1;
    this.handleShowCreate = this.handleShowCreate.bind(this);
    this.handleShowManage = this.handleShowManage.bind(this);
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

  handleShowManage(){
    let newState = Object.assign({}, this.state);
    newState.showManage = !newState.showManage;
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
                        <Button variant={chat.variant}
                                onClick={(evt) => {
                                  this.managedChat = chat.chatId;
                                  this.handleShowManage();
                                }}>
                                <FontAwesomeIcon icon={faTools} />
                        </Button>
                      </InputGroup.Append>
                    </InputGroup>
                  )
                }
            })
          }

        <CreateModal handler={this.handleShowCreate} show={this.state.showCreate} />
        <ManageModal handler={this.handleShowManage}
                     isAdmin={this.isAdmin}
                     show={this.state.showManage}
                     chatId={this.managedChat} />
      </div>
    );
  }
}

export default ChatList;
