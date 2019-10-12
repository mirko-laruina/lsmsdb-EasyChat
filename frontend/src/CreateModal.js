import React, {Component} from 'react'
import {Modal, FormControl, Form, Alert, Button, InputGroup} from 'react-bootstrap'

class CreateModal extends Component {
    constructor(){
        super()
        this.state = {
            wrongUser: 'hidden'
        }
    }

    render(){
        return (
            <Modal show={this.props.show} onHide={this.props.handler}>
                <Modal.Header closeButton>
                <Modal.Title>Add chat</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                <Alert variant="danger" className={this.state.wrongUser ? 'hidden' : ''}>
                No user found
                </Alert>
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
                    <Button block onClick={this.props.handler} variant="outline-success">Add</Button>
                </Form>
                </Modal.Body>
            </Modal>
    )}
}

export default CreateModal;