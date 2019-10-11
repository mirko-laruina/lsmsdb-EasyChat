import React, {Component} from 'react';
import { Button, Form, Card } from "react-bootstrap";
import Cookies from 'universal-cookie';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import './login.css';

const cookies = new Cookies();

class LoginForm extends Component {
  constructor(){
    //Controllare se lo stato serve poi
    super();
    this.state = {
      username: '',
      password: '',
      wrongAuth: 'hidden',
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
    var self = this;
    axios.post('http://localhost:8080/api/v1/auth/login', null,{ params: {
      username: this.state.username,
      password: this.state.password
    }})
    .then(function (response) {
        if(response['data']['success']){
          self.setState({
            wrongAuth: 'hidden',
          })
          cookies.set('sessionId', response['data']['sessionId']);
        } else {
          self.setState({
            wrongAuth: '',
          })        
        }
    })
    .catch(function (error) {
      console.log(error);
    });
  }

  render() {
    return (
      <div className="Login">
        <Form onSubmit={this.handleSubmit}>
          <h2>EasyChat</h2>
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
          <Card body bg="danger"
                style={{ marginBottom: '20px'}}
                className={this.state.wrongAuth ? 'hidden' : ''}>Wrong username or password</Card>
          <Button variant="primary" type="submit">
            Submit
          </Button>
        </Form>
      </div>
    );
  }
}

export default LoginForm;
