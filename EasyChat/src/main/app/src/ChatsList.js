import React, {Component} from 'react';
import { ButtonGroup, Button, InputGroup } from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.min.css';
import axios from 'axios';
import CreateModal from './CreateModal.js'
import ManageModal from './ManageModal.js'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faTools } from '@fortawesome/free-solid-svg-icons'
import Cookies from 'universal-cookie';
const cookies = new Cookies();

class ChatList extends Component {

  constructor(){
    super();
    this.state = {
      chatList: [],
      showCreate: false,
      showManage: false
    }
    this.selectedChat = -1;
    this.managedChat = -1;
    this.handleShowCreate = this.handleShowCreate.bind(this);
    this.handleShowManage = this.handleShowManage.bind(this);
    this.handleLogout = this.handleLogout.bind(this);
  }

  componentDidMount(){
    this.getChat();
    window.setInterval(()=>{
      this.getChat();
    }, 1000)
  }

  getChat(){
    var self = this;
    axios.get('http://'+window.location.hostname+':8080/api/v1/chats',{ params: {
      sessionId: this.props.sid,
    }})
    .then(function (response) {
        response.data.some((chat, i) => {
            //we have to find a better way
            if(self.state.chatList.length > 0){
              self.state.chatList.forEach((oldChat) => {
                if(oldChat.chatId === chat.chatId){
                  chat.variant = oldChat.variant
                  if(chat.variant === "warning"){
                    self.selectedChat = i;
                  }
                  return true;
                }
              })
            } else {
              chat.variant = "light"
            }
            return false;
        })
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

  handleLogout(){
    axios.post('http://'+window.location.hostname+':8080/api/v1/auth/logout', null, {params: {
      sessionId: this.props.sid
    }})
    .then(function(response){
      //if success or not, the user should relog
      //success could be false if sid is expired
      cookies.set('sessionId', '');
      window.location.reload();
    })
    .catch(function(error){
      console.log(error);
    })
  }

  render() {
    return (
      <div className="chatsList">
        <ButtonGroup vertical>
          <Button className="chatLabel border logout"
                  variant="outline-danger"
                  onClick={this.handleLogout}>Logout
          </Button>
          <Button className="chatLabel border addChat"
                  variant="outline-success"
                  onClick={this.handleShowCreate}>+ Create
          </Button>
        </ButtonGroup>
          {
            this.state.chatList.map((chat, i) => {
                if(chat.members.length < 3){
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
                                  this.isAdmin = chat.isAdmin;
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

        <CreateModal sid={this.props.sid} handler={this.handleShowCreate} show={this.state.showCreate} />
        <ManageModal sid={this.props.sid}
                     handler={this.handleShowManage}
                     isAdmin={this.isAdmin}
                     show={this.state.showManage}
                     chatId={this.managedChat} />
      </div>
    );
  }
}

export default ChatList;
