from pytorch/pytorch:latest

RUN pip install pytorch_pretrained_bert numpy pandas nltk Flask flask-cors transformers

COPY main.py main.py
COPY finbert finbert
COPY models models

EXPOSE  8087
CMD ["python3", "main.py"]
