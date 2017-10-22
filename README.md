# AIComposer
An AI that uses LTSM to generated music. Its goals are to explore the limit of computational creavitiy as well as assisting music making.

## Inspiration
Creating music has always been seen as a laborious process for artists. We thought of how we could assist them in aspects that would retain the originality and creativity of the composer, but still giving a helpful boost to the creator's creativity when needed. 

## What it does
Using Tensorflow, Google Magenta's Machine Learning Library, and a lot of conditional coding, we created a program that takes an input of a short tune and returns a full melody. The AI takes the user input and processes it through its recurrent neural network. It then generates a new set of notes to continue the sequence that would be most likely to please the ear. The AI uses LSTM (Long Short Term Memory) in order to emulate "human creativity" by analyzing previously composed music. 

## How we built it
The code used to connect the AI was mainly coded in JavaScript, but the actual processing of the AI "thinking" is coded in Python. The interactive portions of the AI are connected through HTML and Javascript, as using MIDI as a method of quantifying the music. 

## Challenges we ran into
The communication between the frontend of the project and the backend of the project was difficult to connect, but we were able to resolve the problem by personalizing each step of the communication protocol. Initiating Fast Fourier Transfer (note recognition methodology) to distinguish notes in recorded audio was a laborious effort, but we were able to learn a lot more about how audio file manipulation worked. 

## Accomplishments that we're proud of
We are very satisfied with the overall project, as our final product is able to successfully produce melodies that highly resemble those composed by a real human. As well, our interface is very easy for users to engage with, making the music making process even easier. 

## What we learned
Throughout the development of our project, we were able to witness the potential that machine learning and AI are capable of. We were impressed with the work that computers could generate, as it so closely simulates that of human creativity. The project was a real eye-opener as we reflected upon the limits of AI. 

## What's next for Composer Companion
As we now have a working prototype, we plan to expand upon how much the AI interacts with the music, allowing a future iteration to be able to create even more human-like music. We also see this project as having the potential to grow into a business, centred around music production and AI interaction. As of right now, we plan to keep our project as an open source repository for others who would be interested to interact with, and even build upon. 
