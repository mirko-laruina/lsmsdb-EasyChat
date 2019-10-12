import React, {Component} from 'react'
import {Modal, Form, Button} from 'react-bootstrap'

class ManageModal extends Component{
    render(){
        return (
            <Modal show={this.props.show} onHide={this.props.handler}>
            <Modal.Header closeButton>
            <Modal.Title>Users</Modal.Title>
            </Modal.Header>
            <Modal.Body>
            
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