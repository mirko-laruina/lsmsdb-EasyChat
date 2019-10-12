import React, {Component} from 'react';
import {Button, InputGroup, FormControl, Form} from 'react-bootstrap'

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
        console.log(this.state.value)
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
                    this.props.chatId == 0 ? null : (
                        <InputGroup size="lg">
                            <FormControl    as="textarea"
                                            rows="2"
                                            aria-label="Send"
                                            placeholder="Write your message!"
                                            value={this.state.value}
                                            onKeyPress={(evt) => {
                                                    if(evt.which==13){
                                                        evt.preventDefault();
                                                        evt.target.form.dispatchEvent(new Event("submit", {cancelable: true}));
                                                    }
                                                }
                                            }
                                            onChange={this.handleChange}/>
                            <InputGroup.Append>
                                <Button type="submit">Send</Button>
                            </InputGroup.Append>
                        </InputGroup>
                    )}
                    </Form>
            </div>
        )
    }
}

export default ChatSend;