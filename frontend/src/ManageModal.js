import React, {Component} from 'react'
import {Modal, Form, Button} from 'react-bootstrap'
import axios from 'axios'

class ManageModal extends Component{

    constructor(){
        super();
        this.state = {
            members: [],
        }

        this.getMembers = this.getMembers.bind(this)
    }

    componentDidUpdate(prevPops){
        if(this.props.chatId !== prevPops.chatId){
            this.getMembers();
        }
    }

    getMembers(){
        if(this.props.chatId === -1){
            return
        }
        var self = this;
        axios.get('http://localhost:8080/api/v1/chat/'+this.props.chatId, {params : {
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
            <Form>
                <Form.Group>
                <Form.Control placeholder="User to add"></Form.Control>
                </Form.Group>
                <Button block   disabled={!this.props.isAdmin}
                                onClick={this.props.handler}
                                variant={this.props.isAdmin ? 'outline-success' : 'secondary'}>
                                    Add
                </Button>
            </Form>
            </Modal.Body>
            </Modal>
        )
    }
}

export default ManageModal;