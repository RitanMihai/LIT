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

describe('Dividend e2e test', () => {
  const dividendPageUrl = '/dividend';
  const dividendPageUrlPattern = new RegExp('/dividend(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const dividendSample = { dateRecived: '2022-03-28T16:12:40.224Z' };

  let dividend: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/portfolio/api/dividends+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/portfolio/api/dividends').as('postEntityRequest');
    cy.intercept('DELETE', '/services/portfolio/api/dividends/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (dividend) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/portfolio/api/dividends/${dividend.id}`,
      }).then(() => {
        dividend = undefined;
      });
    }
  });

  it('Dividends menu should load Dividends page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('dividend');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Dividend').should('exist');
    cy.url().should('match', dividendPageUrlPattern);
  });

  describe('Dividend page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(dividendPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Dividend page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/dividend/new$'));
        cy.getEntityCreateUpdateHeading('Dividend');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', dividendPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/portfolio/api/dividends',
          body: dividendSample,
        }).then(({ body }) => {
          dividend = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/portfolio/api/dividends+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/portfolio/api/dividends?page=0&size=20>; rel="last",<http://localhost/services/portfolio/api/dividends?page=0&size=20>; rel="first"',
              },
              body: [dividend],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(dividendPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Dividend page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('dividend');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', dividendPageUrlPattern);
      });

      it('edit button click should load edit Dividend page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Dividend');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', dividendPageUrlPattern);
      });

      it('last delete button click should delete instance of Dividend', () => {
        cy.intercept('GET', '/services/portfolio/api/dividends/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('dividend').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', dividendPageUrlPattern);

        dividend = undefined;
      });
    });
  });

  describe('new Dividend page', () => {
    beforeEach(() => {
      cy.visit(`${dividendPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Dividend');
    });

    it('should create an instance of Dividend', () => {
      cy.get(`[data-cy="dateRecived"]`).type('2022-03-28T05:31').should('have.value', '2022-03-28T05:31');

      cy.get(`[data-cy="taxRate"]`).type('53759').should('have.value', '53759');

      cy.get(`[data-cy="totalRecived"]`).type('90048').should('have.value', '90048');

      cy.get(`[data-cy="dividendType"]`).select('PROPERTY_INCOME');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        dividend = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', dividendPageUrlPattern);
    });
  });
});
