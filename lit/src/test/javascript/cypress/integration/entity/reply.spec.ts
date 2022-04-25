import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Reply e2e test', () => {
  const replyPageUrl = '/reply';
  const replyPageUrlPattern = new RegExp('/reply(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const replySample = { content: 'hack' };

  let reply: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/social/api/replies+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/social/api/replies').as('postEntityRequest');
    cy.intercept('DELETE', '/services/social/api/replies/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (reply) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/social/api/replies/${reply.id}`,
      }).then(() => {
        reply = undefined;
      });
    }
  });

  it('Replies menu should load Replies page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('reply');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Reply').should('exist');
    cy.url().should('match', replyPageUrlPattern);
  });

  describe('Reply page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(replyPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Reply page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/reply/new$'));
        cy.getEntityCreateUpdateHeading('Reply');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', replyPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/social/api/replies',
          body: replySample,
        }).then(({ body }) => {
          reply = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/social/api/replies+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/social/api/replies?page=0&size=20>; rel="last",<http://localhost/services/social/api/replies?page=0&size=20>; rel="first"',
              },
              body: [reply],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(replyPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Reply page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('reply');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', replyPageUrlPattern);
      });

      it('edit button click should load edit Reply page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Reply');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', replyPageUrlPattern);
      });

      it('last delete button click should delete instance of Reply', () => {
        cy.intercept('GET', '/services/social/api/replies/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('reply').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', replyPageUrlPattern);

        reply = undefined;
      });
    });
  });

  describe('new Reply page', () => {
    beforeEach(() => {
      cy.visit(`${replyPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Reply');
    });

    it('should create an instance of Reply', () => {
      cy.get(`[data-cy="content"]`).type('Forge generating').should('have.value', 'Forge generating');

      cy.get(`[data-cy="date"]`).type('2022-03-27T22:45').should('have.value', '2022-03-27T22:45');

      cy.get(`[data-cy="language"]`).select('RO');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        reply = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', replyPageUrlPattern);
    });
  });
});
