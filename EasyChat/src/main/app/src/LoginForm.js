import React, {Component} from 'react';
import { Button, Form, Alert } from "react-bootstrap";
import Cookies from 'universal-cookie';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import './login.css';

const cookies = new Cookies();
const API_URL = 'http://'+window.location.hostname+':8080/api/v1/'

class LoginForm extends Component {
  constructor(){
    super();
    this.state = {
      username: '',
      password: '',
      wrongAuth: 'hidden',
      registerPage: false
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
    var postUrl;
    this.setState({
      wrongAuth: 'hidden',
    })
    postUrl = API_URL
    if(!this.state.registerPage){
      postUrl += 'auth/login';
    } else {
      postUrl += 'users';
    }

    axios.post(postUrl, {
      username: this.state.username,
      password: this.state.password
    })
    .then(function (response) {
        if(response.data.success){
          self.setState({
            wrongAuth: 'hidden',
          })
          cookies.set('sessionId', response.data.sessionId);
          cookies.set('username', self.state.username);
          window.location.reload();
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
          <Form onSubmit={(evt) => this.handleSubmit(evt, this.props)}>
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

            {this.state.registerPage ? (
                <div>
                <Alert variant="danger"
                  style={{ marginBottom: '20px'}}
                  className={this.state.wrongAuth ? 'hidden' : ''}>Username already taken</Alert>
                  <Button variant="link" onClick={(evt) => this.setState({registerPage: false})}>Log in</Button>
                <Button variant="primary" type="submit">
                  Sign on!
                </Button>
                </div>
                ) : (
                <div>
                <Alert variant="danger"
                  style={{ marginBottom: '20px'}}
                  className={this.state.wrongAuth ? 'hidden' : ''}>Wrong username or password</Alert>
                 <Button variant="link" onClick={(evt) => this.setState({registerPage: true})}>Sign on!</Button>
                <Button variant="primary" type="submit">
                  Log in!
                </Button>
                </div>
                )}
          </Form>
      </div>
    );
  }
}

export default LoginForm;
