import React, {Component} from 'react';
import { Button, Form } from "react-bootstrap";
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import './login.css';

class LoginForm extends Component {
  constructor(){
    //Controllare se lo stato serve poi
    super();
    this.state = {
      username: '',
      password: ''
    };

    this.handleSubmit = this.handleSubmit.bind(this)
  }

  handlePw(value){
    this.setState({
      password: value
    });
  }

  handleUser(value){
    this.setState({
      username: value
    });
  }

  handleSubmit(evt){
    evt.preventDefault();
    /*.post('http://localhost:8080/login', {
      username: this.state.username,
      password: this.state.password
    })
    .then(function (response) {
      console.log(response);
    })
    .catch(function (error) {
      console.log(error);
    });*/
  }

  render() {
    return (
      <div className="Login">
        <Form onSubmit={this.handleSubmit}>
          <Form.Group controlId="formUsername">
            <Form.Label>Username</Form.Label>
            <Form.Control type="text"placeholder="Enter username"
                          value={this.state.username} onChange={(evt) => this.handleUser(evt.target.value)}/>
          </Form.Group>

          <Form.Group controlId="formPassword">
            <Form.Label>Password</Form.Label>
            <Form.Control type="password" placeholder="Password"
                          value={this.state.password} onChange={(evt) => this.handlePw(evt.target.value)} />
          </Form.Group>
          <Button variant="primary" type="submit">
            Submit
          </Button>
        </Form>
      </div>
    );
  }
}

export default LoginForm;
