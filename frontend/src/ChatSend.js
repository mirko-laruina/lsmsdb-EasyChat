import React, {Component} from 'react';
import {Button, InputGroup, FormControl, Form} from 'react-bootstrap'
import axios from 'axios';

class ChatSend extends Component {

    constructor(){
        super();
        this.state = {
            value: ""
        }
        this.handleChange = this.handleChange.bind(this)
        this.sendMessage = this.sendMessage.bind(this)
    }

    sendMessage(evt){
        evt.preventDefault();
        axios.post('http://localhost:8080/api/v1/chat/'+this.props.chatId+'/messages', {
            text: this.state.value
        },{ params: {
            sessionId: this.props.sid
        }})
        .then(function(response){
            if(!response.data.success){
                alert('Message not send');
            }
        })
        .catch(function(error){
            console.log(error);
        })
        this.setState({ value: '' })
    }

    handleChange(evt){
        this.setState({
            value: evt.target.value
        })
    }

    render(){
        return (
            <div className="sendContainer">
                <Form onSubmit={this.sendMessage}>
                {
                    this.props.chatId === 0 ? null : (
                        <InputGroup size="lg">
                            <FormControl    as="textarea"
                                            rows="2"
                                            aria-label="Send"
                                            placeholder="Write your message!"
                                            value={this.state.value}
                                            onKeyPress={(evt) => {
                                                    if(evt.which===13){
                                                        evt.preventDefault();
                                                        evt.target.form.dispatchEvent(new Event("submit", {cancelable: true}));
                                                    }
                                                }
                                            }
                                            onChange={this.handleChange}/>
                            <InputGroup.Append>
                                <Button type="submit" variant="primary">Send</Button>
                            </InputGroup.Append>
                        </InputGroup>
                    )}
                    </Form>
            </div>
        )
    }
}

export default ChatSend;