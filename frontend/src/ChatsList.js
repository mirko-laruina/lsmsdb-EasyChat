import React, {Component} from 'react';
import { ButtonGroup, Button, Modal, InputGroup, FormControl } from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.min.css';
import axios from 'axios';

class ChatList extends Component {

  constructor(){
    super();
    this.state = {
      chatList: [],
      show: false,
    }
    this.selectedChat = -1;
    this.handleShowAdd = this.handleShowAdd.bind(this)
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

  handleShowAdd(){
    let newState = Object.assign({}, this.state);
    newState.show = !newState.show;
    this.setState(newState);
  }

  render() {
    return (
      <div className="chatsList">
        <ButtonGroup vertical> 
          <Button className="chatLabel border addChat"
                  variant="outline-success"
                  onClick={this.handleShowAdd}>+ Create</Button>
          
          {
            this.state.chatList.map((chat, i) => {
                      return <Button className="chatLabel border"
                              variant={chat.variant}
                              key={chat.chatId}
                              onClick={() => this.changeChat(chat.chatId, i)}
                              >
                        {chat.name}
                      </Button>
            })
          }
        </ButtonGroup>

        <Modal show={this.state.show} onHide={this.handleShowAdd}>
            <Modal.Header closeButton>
              <Modal.Title>Add chat</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <InputGroup>
                <FormControl  aria-label="Add"
                              placeholder="Who do you want to chat to?"></FormControl>
                <InputGroup.Append>
                    <Button type="submit"
                            variant="outline-success">Add</Button>
                </InputGroup.Append>
              </InputGroup>
            </Modal.Body>
            <Modal.Header>
              <Modal.Title>Create group</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <InputGroup>
                <FormControl  aria-label="Create group"
                              placeholder="Name of your group"></FormControl>
                <InputGroup.Append>
                    <Button type="submit"
                            variant="outline-success">Create</Button>
                </InputGroup.Append>
              </InputGroup>
            </Modal.Body>
            <Modal.Footer>
              <Button onClick={this.handleShowAdd} variant="outline-danger">Close</Button>
            </Modal.Footer>
          </Modal>
      </div>
    );
  }
}

export default ChatList;
