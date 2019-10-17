import React, {Component} from 'react'
import {Modal, Form, Button, Alert} from 'react-bootstrap'
import axios from 'axios'

class ManageModal extends Component{

    constructor(){
        super();
        this.state = {
            members: [],
            wrongUser: 'hidden',
            newUser: '',
        }

        this.getMembers = this.getMembers.bind(this)
        this.addToGroup = this.addToGroup.bind(this)
        this.deleteGroup= this.deleteGroup.bind(this)
    }

    componentDidUpdate(prevPops){
        if(this.props.chatId !== prevPops.chatId || this.props.show !== prevPops.show){
            this.getMembers();
        }
    }

    getMembers(){
        if(this.props.chatId === -1){
            return
        }
        var self = this;
        axios.get('http://'+window.location.hostname+':8080/api/v1/chat/'+this.props.chatId, {params : {
            sessionId: this.props.sid,
        }})
        .then(function(response){
            if(response.data){
                self.setState({
                    members: response.data.members
                })
            }
        })
        .catch(function(error){
            console.log(error);
        })
    }

    handlerNewUser(evt){
        this.setState({
            newUser: evt.target.value,
        })
    }

    addToGroup(evt){
        if(this.props.chatId === -1){
            return
        }
        var self = this;
        evt.preventDefault();
        console.log(this.state)
        axios.post('http://'+window.location.hostname+':8080/api/v1/chat/'+this.props.chatId+'/members', {
            username: this.state.newUser,
        },{ params: {
            sessionId: this.props.sid,
        }})
        .then(function(response){
            if(response.data.success){
                self.props.handler();
                self.setState({
                    wrongUser: 'hidden',
                    newUser: '',
                })
            } else {
                self.setState({
                    wrongUser: '',
                })
            }
        })
        .catch(function(error){
            console.log(error);
        })
    }

    deleteGroup(){
        alert("hihi");
    }

    render(){
        return (
            <Modal show={this.props.show} onHide={this.props.handler}>
                <Modal.Header closeButton>
                    <Modal.Title>Users</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {   this.state.members.map((member, i) => {
                        return <p key={i}>{member.username}</p>
                        })
                    }
                </Modal.Body>
                <Modal.Header>
                    <Modal.Title>Add user</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={this.addToGroup}>
                        <Form.Group>
                            <Alert variant="danger" className={this.state.wrongUser}>
                                Wrong username
                            </Alert>
                            <Form.Control   value={this.state.newUser}
                                            onChange={(evt) => this.handlerNewUser(evt)}
                                            placeholder="User to add"></Form.Control>
                        </Form.Group>
                        <Button block   disabled={!this.props.isAdmin}
                                        type="submit"
                                        variant={this.props.isAdmin ? 'outline-success' : 'secondary'}>
                                            Add
                        </Button>
                    </Form>
                </Modal.Body>
                <Modal.Header>
                    <Modal.Title>Delete group</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={this.deleteGroup}>
                        <Button block   disabled={!this.props.isAdmin}
                                type="submit"
                                variant={this.props.isAdmin ? 'outline-danger' : 'secondary'}>
                            Delete group
                        </Button>
                    </Form>
                </Modal.Body>
            </Modal>
        )
    }
}

export default ManageModal;