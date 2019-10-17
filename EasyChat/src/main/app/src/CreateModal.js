import React, {Component} from 'react'
import {Modal, FormControl, Form, Alert, Button, InputGroup} from 'react-bootstrap'
import axios from "axios"
const API_URL = 'http://'+window.location.hostname+':8080/api/v1/'

class CreateModal extends Component {
    constructor(){
        super()
        this.state = {
            wrongUser: 'hidden',
            wrongGroup: 'hidden',
            newChatValue: '',
            newGroupValue: '',
            newUser1Value: '',
            newUser2Value: '',
        }

        this.createNewChat = this.createNewChat.bind(this)
        this.createNewGroup = this.createNewGroup.bind(this)
    }

    handlerChatValue(value){
        this.setState({
            newChatValue: value,
        })
    }

    createNewChat(evt){
        evt.preventDefault();
        var self = this;
        axios.post(API_URL + "chats", {
            name: 'null',
            members: [this.state.newChatValue]
        }, { params: {
            sessionId: this.props.sid,
        }})
        .then(function(response){
            if(!response.data.success){
                self.setState({
                    wrongUser: '',
                })
            } else {
                window.location.reload();
            }
        })
        .catch(function(error){
            console.log(error);
        })
    }

    createNewGroup(evt){
        evt.preventDefault();
        var self = this;
        axios.post(API_URL + "chats", {
            name: this.state.newGroupValue,
            members: [this.state.newUser1Value, this.state.newUser2Value]
        }, { params: {
            sessionId: this.props.sid,
        }})
        .then(function(response){
            if(!response.data.success){
                self.setState({
                    wrongGroup: '',
                })
            } else {
                window.location.reload();
            }
        })
        .catch(function(error){
            console.log(error);
        })
    }

    render(){
        return (
            <Modal show={this.props.show} onHide={this.props.handler}>
                <Modal.Header closeButton>
                <Modal.Title>Add chat</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                <Alert variant="danger" className={this.state.wrongUser}>
                No user found
                </Alert>
                <Form onSubmit={this.createNewChat}>
                    <InputGroup>
                        <FormControl  aria-label="Add"
                                    placeholder="Who do you want to chat to?"
                                    value={this.state.newChatValue}
                                    onChange={(evt) => this.handlerChatValue(evt.target.value)}></FormControl>
                        <InputGroup.Append>
                            <Button type="submit"
                                    variant="outline-success">Add</Button>
                        </InputGroup.Append>
                    </InputGroup>
                </Form>
                </Modal.Body>
                <Modal.Header>
                <Modal.Title>Create group</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                <Form onSubmit={this.createNewGroup}>
                    <Alert variant="danger" className={this.state.wrongGroup}>
                    Type 2 valid usernames!
                    </Alert>
                    <Form.Group>
                    <Form.Control   aria-label="Create group"
                                    placeholder="Name of your group"
                                    onChange={(evt) => {
                                        this.setState({
                                            newGroupValue: evt.target.value
                                    })}}></Form.Control>
                    </Form.Group>
                    <Form.Group>
                    <Form.Control   placeholder="User to add"
                                    onChange={(evt) => {
                                        this.setState({
                                            newUser1Value: evt.target.value
                                    })}}></Form.Control>
                    </Form.Group>
                    <Form.Group>
                    <Form.Control   placeholder="User to add"
                                    onChange={(evt) => {
                                        this.setState({
                                            newUser2Value: evt.target.value
                                    })}}></Form.Control>
                    </Form.Group>
                    <Button block type="submit" variant="outline-success">Create group</Button>
                </Form>
                </Modal.Body>
            </Modal>
    )}
}

export default CreateModal;