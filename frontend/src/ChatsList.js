import React, {Component} from 'react';
import { ButtonGroup, Button, Modal, InputGroup, FormControl, Form } from "react-bootstrap";
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
    this.handleShowCreate = this.handleShowCreate.bind(this)
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
    newState.show = !newState.show;
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
                    <InputGroup className="border">
                        <Button variant={chat.variant}
                                className="groupLabel"
                                key={chat.chatId}
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

        <Modal show={this.state.show} onHide={this.handleShowCreate}>
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
              <Form>
                <Form.Group>
                  <Form.Control  aria-label="Create group"
                              placeholder="Name of your group"></Form.Control>
                </Form.Group>
                <Form.Group>
                  <Form.Control placeholder="User to add"></Form.Control>
                </Form.Group>
                <Form.Group>
                  <Form.Control placeholder="User to add"></Form.Control>
                </Form.Group>
                <Button block onClick={this.handleShowCreate} variant="outline-success">Add</Button>
              </Form>
            </Modal.Body>
          </Modal>
      </div>
    );
  }
}

export default ChatList;
