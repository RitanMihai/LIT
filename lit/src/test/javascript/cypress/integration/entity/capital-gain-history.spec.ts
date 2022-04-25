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

describe('CapitalGainHistory e2e test', () => {
  const capitalGainHistoryPageUrl = '/capital-gain-history';
  const capitalGainHistoryPageUrlPattern = new RegExp('/capital-gain-history(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const capitalGainHistorySample = { date: '2022-03-28' };

  let capitalGainHistory: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/watcher/api/capital-gain-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/watcher/api/capital-gain-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/services/watcher/api/capital-gain-histories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (capitalGainHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/watcher/api/capital-gain-histories/${capitalGainHistory.id}`,
      }).then(() => {
        capitalGainHistory = undefined;
      });
    }
  });

  it('CapitalGainHistories menu should load CapitalGainHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('capital-gain-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CapitalGainHistory').should('exist');
    cy.url().should('match', capitalGainHistoryPageUrlPattern);
  });

  describe('CapitalGainHistory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(capitalGainHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CapitalGainHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/capital-gain-history/new$'));
        cy.getEntityCreateUpdateHeading('CapitalGainHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', capitalGainHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/watcher/api/capital-gain-histories',
          body: capitalGainHistorySample,
        }).then(({ body }) => {
          capitalGainHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/watcher/api/capital-gain-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/watcher/api/capital-gain-histories?page=0&size=20>; rel="last",<http://localhost/services/watcher/api/capital-gain-histories?page=0&size=20>; rel="first"',
              },
              body: [capitalGainHistory],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(capitalGainHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CapitalGainHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('capitalGainHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', capitalGainHistoryPageUrlPattern);
      });

      it('edit button click should load edit CapitalGainHistory page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CapitalGainHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', capitalGainHistoryPageUrlPattern);
      });

      it('last delete button click should delete instance of CapitalGainHistory', () => {
        cy.intercept('GET', '/services/watcher/api/capital-gain-histories/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('capitalGainHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', capitalGainHistoryPageUrlPattern);

        capitalGainHistory = undefined;
      });
    });
  });

  describe('new CapitalGainHistory page', () => {
    beforeEach(() => {
      cy.visit(`${capitalGainHistoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CapitalGainHistory');
    });

    it('should create an instance of CapitalGainHistory', () => {
      cy.get(`[data-cy="date"]`).type('2022-03-27').should('have.value', '2022-03-27');

      cy.get(`[data-cy="capitalGain"]`).type('10917').should('have.value', '10917');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        capitalGainHistory = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', capitalGainHistoryPageUrlPattern);
    });
  });
});
