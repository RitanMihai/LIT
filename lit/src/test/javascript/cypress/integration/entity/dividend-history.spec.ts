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

describe('DividendHistory e2e test', () => {
  const dividendHistoryPageUrl = '/dividend-history';
  const dividendHistoryPageUrlPattern = new RegExp('/dividend-history(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const dividendHistorySample = { date: '2022-03-27' };

  let dividendHistory: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/watcher/api/dividend-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/watcher/api/dividend-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/services/watcher/api/dividend-histories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (dividendHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/watcher/api/dividend-histories/${dividendHistory.id}`,
      }).then(() => {
        dividendHistory = undefined;
      });
    }
  });

  it('DividendHistories menu should load DividendHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('dividend-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('DividendHistory').should('exist');
    cy.url().should('match', dividendHistoryPageUrlPattern);
  });

  describe('DividendHistory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(dividendHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create DividendHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/dividend-history/new$'));
        cy.getEntityCreateUpdateHeading('DividendHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', dividendHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/watcher/api/dividend-histories',
          body: dividendHistorySample,
        }).then(({ body }) => {
          dividendHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/watcher/api/dividend-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/watcher/api/dividend-histories?page=0&size=20>; rel="last",<http://localhost/services/watcher/api/dividend-histories?page=0&size=20>; rel="first"',
              },
              body: [dividendHistory],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(dividendHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details DividendHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('dividendHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', dividendHistoryPageUrlPattern);
      });

      it('edit button click should load edit DividendHistory page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DividendHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', dividendHistoryPageUrlPattern);
      });

      it('last delete button click should delete instance of DividendHistory', () => {
        cy.intercept('GET', '/services/watcher/api/dividend-histories/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('dividendHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', dividendHistoryPageUrlPattern);

        dividendHistory = undefined;
      });
    });
  });

  describe('new DividendHistory page', () => {
    beforeEach(() => {
      cy.visit(`${dividendHistoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('DividendHistory');
    });

    it('should create an instance of DividendHistory', () => {
      cy.get(`[data-cy="date"]`).type('2022-03-28').should('have.value', '2022-03-28');

      cy.get(`[data-cy="dividend"]`).type('56072').should('have.value', '56072');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        dividendHistory = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', dividendHistoryPageUrlPattern);
    });
  });
});
